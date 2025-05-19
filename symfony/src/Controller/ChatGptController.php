<?php
namespace App\Controller;

use App\Service\ChatGptService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class ChatGptController extends AbstractController
{
    #[Route('/chat', name: 'chat')]
    public function index(): Response
    {
        return $this->render('collaboration/collaboration.html.twig');
    }

    #[Route('/chat/ask', name: 'chat_ask', methods: ['POST'])]
    public function ask(Request $request, ChatGptService $chatGptService): JsonResponse
    {
        $question = $request->request->get('question');
        $response = $chatGptService->askChatGpt($question);

        return new JsonResponse(['response' => $response]);
    }
}
